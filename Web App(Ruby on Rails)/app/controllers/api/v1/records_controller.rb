class Api::V1::RecordsController < Api::ApiController
  respond_to :json
  require 'uri'
  before_action :authenticate, only: [:create, :increment_view, :increment_like]

  def create
    if @user.id != params[:user_id].to_i
      response = { :message => "wrong user" }
      render json: response.to_json, status: 400
    end
    @record = Record.new({:title => URI.unescape(params[:title]), :description => URI.unescape(params[:description]), :tag_list => URI.unescape(params[:tag]), :file => params[:file], :category_id => params[:category_id], :private => params[:private], :user_id => @user.id})
    if @record.save
      response = { :message => "Record saved", :data => {:record_id => @record.id} }
      render json: response.to_json, status: 200
    else
      response = { :message => @record.errors.messages }
      render json: response.to_json, status: 400
    end
  end

  def show
    @record = Record.find(params[:id])
    if !@record.nil?
      @comments = []
      comments = @record.comments.order(:created_at).reverse_order.includes(:user)
      comments.each do |c|
        @comments << {:nick_name => c.user.nick_name, :description => c.description, :created_at => c.created_at}
      end
      response = { :message => "Record found", :data => {:profile_path => @record.user.profile_pic.url(:thumb), :nick_name => @record.user.nick_name, :record => @record, :comments => @comments} }
      render json: response.to_json, status: 200
    else
      response = { :message => "Record not found" }
      render json: response.to_json, status: 400
    end
  end

  def increment_view
    if @user.id != params[:user_id].to_i
      response = { :message => "wrong user" }
      render json: response.to_json, status: 400
    end
    @record = Record.find(params[:record_id])
    if !@record.nil?
      @record.update(view:@record.view + 1)
      response = { :message => "success" }
      render json: response.to_json, status: 200
    else
      response = { :message => "record does not exist" }
      render json: response.to_json, status: 400
    end
  end

  def increment_like
    if @user.id != params[:user_id].to_i
      response = { :message => "wrong user" }
      render json: response.to_json, status: 400
    end
    @record = Record.find(params[:record_id])
    if !@record.nil?
      a = UserLikesRecords.new({:user_id => @user.id, :record_id => @record.id})
      if !a.save
        @error = "You already liked this audio."
      else
        @record.update_attribute('like', @record.like + 1)
        if @record.user_id != @user.id
          Notification.create({:user_id => @record.user_id, :category => 0, :who_id => @user.id, :what_id => @record.id})
        end
        u = @record.user
        u.update_attribute('fame', u.fame + 1)
      end
      if @error.nil?
        response = { :message => "success" }
        render json: response.to_json, status: 200
      else
        response = { :message => "error in saving like" }
        render json: response.to_json, status: 200
      end
    else
      response = { :message => "record does not exist" }
      render json: response.to_json, status: 400
    end
  end

  private
    def record_params
      params.permit(:user_id, :title, :file, :description, :category_id, :private)
    end
end