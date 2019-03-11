class Api::V1::RecordsController < Api::ApiController
  respond_to :json
  require 'uri'
  before_action :authenticate, only: [:create]

  def create
    if @user.id != params[:user_id].to_i
      response = { :message => "wrong user" }
      render json: response.to_json, status: 400
    end
    @comment = Comment.new({:user_id => @user.id, :record_id => params[:record_id], :description => params[:comment], :created_at => Time.now})
        
    if @comment.save
      response = { :message => "Comment saved"}
      render json: response.to_json, status: 200
    else
      response = { :message => @comment.errors.messages }
      render json: response.to_json, status: 400
    end
  end

  private
    def record_params
      params.permit(:user_id, :title, :file, :description, :category_id, :private)
    end
end