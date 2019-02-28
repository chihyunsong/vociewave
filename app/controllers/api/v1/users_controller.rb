class Api::V1::UsersController < Api::ApiController
  respond_to :json
  before_action :authenticate, :except=> [:create]

  def create
    params[:user] = params
    user = User.new(user_params)
    begin
      if user.save
        response = { :message => "User created.", :data => {:user_id => user.id} }
        render json: response.to_json, status: 200
      else
        response = { :message => user.errors }
        render json: response.to_json, status: 400
      end
    rescue Exception => e
      response = { :message => e.message }
      render json: response.to_json, status: :internal_server_error
    end
  end

  def show
    user = User.find(params[:id])
    if params[:type] == "profile"
      @user.follows?(user) ? is_following = true : is_following = false 
      response = { :message => "user found", :data => {:profile_pic => user.profile_pic.url(:thumb),:nick_name => user.nick_name, :description => user.description, :is_following => is_following} }
      render json: response.to_json, status: 200
    elsif params[:type] == "voice_list"
      if user.id == @user.id
        records = user.records.order(:created_at).reverse_order.includes(:user)
      else
        records = user.records.order(:created_at).reverse_order.where(private:0).includes(:user)
      end
      @records = []
      records.each do |r|
        @records << {:id => r.id, :user_id => r.user_id, :user_nick_name => r.user.nick_name, :title => r.title, :path => r.file.url, :user_profile_path => r.user.profile_pic.url(:thumb)}
      end      
      response = { :data => @records }
      render json: response.to_json, status: 200
    end
  end

  private

  def user_params
    params.require(:user).permit(:email, :nick_name, :password, :password_confirmation)
  end
end
