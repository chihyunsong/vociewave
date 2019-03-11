class UsersController < ApplicationController
  before_action :set_user, only: [:show, :edit, :update, :destroy, :playlists]
  before_action :authenticate_user!, except: [:show, :new, :create, :users_show_more, :playlists]
  respond_to :html, :js
  
  def playlists
    cur_user = current_user
    if !cur_user.nil? and cur_user.id == @user.id
      @self = true # true if current user is viewing his own profile

    end
    if @self
      @playlists = Playlist.order(:updated_at).reverse_order.where(user_id:@user.id)
    else
      @playlists = Playlist.where(private:0).order(:updated_at).reverse_order.where(user_id:@user.id)
    end
    if @playlists.first.nil?
      @playlists = ""
    end
  end

  def show
  if !params[:infoid].nil?
      temp = Notification.where({:user_id => current_user, :id => params[:infoid]}).first
      if !temp.nil?
        temp.destroy
      end
    end
    if !params[:upload_error].nil?
      @show_upload_error_message = false
      if params[:upload_error] == 'true'
        @show_upload_error_message = true
      end
    end
    cur_user = current_user
    if !cur_user.nil? and cur_user.id == @user.id
      @self = true # true if current user is viewing his own profile
      @record = Record.new
    end
    if @self
      @records = @user.records.order(:created_at).reverse_order.includes(:user)
      @length = @records.length
      @likes = @records.map(&:like).sum
    else
      @records = @user.records.order(:created_at).reverse_order.where(private:0)   
    .includes(:user)
    end
    session[:records] = @records.collect(&:id)
    if !params[:playlist].nil?
      if params[:playlist] == 'true'
 
      end
    end    
    respond_with(@user)
  end

  def reset_password
    user = current_user
    user.send_reset_password_instructions
  end

  def edit
    if current_user.id == @user.id
      @self = true # true if current user is viewing his own profile
    else
      redirect_to '/'
    end
  end

  def create
    @user = User.new(user_params)
    @user.save
    respond_with(@user)
  end

  def update
    @user.update(user_params)
    respond_with(@user)
  end
  
  def users_show_more
    if !current_user.nil? and current_user.id == params[:user_id].to_i
      @self = true # true if current user is viewing his own profile
    end
    @offset = params[:id].to_i     
  end

  def playlists_show_more
    if !current_user.nil? and current_user.id == params[:user_id].to_i
      @self = true # true if current user is viewing his own profile
    end
    @offset = params[:id].to_i     
  end

  def update_profile_pic
    if User.file_extension_check(params[:user][:profile_pic].original_filename)
      current_user.update_attribute(:profile_pic, params[:user][:profile_pic])  
    else
      flash[:alert] = "Not an image file."
    end
    redirect_to '/users/edit'
  end

  def voices_i_liked
    if !current_user.nil? and current_user.id == params[:user_id].to_i
      @self = true # true if current user is viewing his own profile
    end
    session[:records] = current_user.liked_records.where(private:false).order(:created_at).reverse_order.includes(:user).collect(&:id)
  end

  def playlists_i_liked
    if !current_user.nil? and current_user.id == params[:user_id].to_i
      @self = true # true if current user is viewing his own profile
    end
    session[:records] = current_user.liked_playlists.where(private:false).order(:created_at).reverse_order.includes(:user).collect(&:id)
  end

  def voices_i_uploaded
    if !current_user.nil? and current_user.id == params[:user_id].to_i
      @self = true # true if current user is viewing his own profile
    end
    session[:records] = current_user.records.order(:created_at).reverse_order.includes(:user).collect(&:id)
  end

  private
    def set_user
      @user = User.find(params[:id])
    end
    

    def user_params
      params.require(:user).permit(:email, :password, :password_confirmation, :nick_name, :provider, :uid, :description)
    end
end
