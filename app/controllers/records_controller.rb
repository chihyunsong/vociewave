class RecordsController < ApplicationController
  before_action :authenticate_user!, except: [:search, :show, :increment_view, :short_comment_list]
  before_action :set_record, only: [:show, :edit, :update, :destroy, :increment_view, :increment_like, :short_comment_list]
  before_action :user_admin!, only: [:destroy]
  respond_to :html, :js

  def destroy
    if current_user.id != @record.user_id
      redirect_to '/'
    else
      # change this using rails dependent destroy later
      Notification.where({:category => 0, :what_id => @record.id}).destroy_all
      Notification.where({:category => 1, :what_id => @record.id}).destroy_all
      
      @record.destroy
      respond_with(@record)
    end
  end

  def update
    if current_user.id != @record.user_id
      redirect_to '/'
    else
      @record.update(update_record_params)
      redirect_to :controller => 'users', :action => 'show', :id => current_user.id
    end
  end

  def show
    if @record.private == 1 and (current_user.nil? || @record.user_id != current_user.id)
        redirect_to '/'
    else
      if !params[:infoid].nil?
        temp = Notification.where({:user_id => current_user, :id => params[:infoid]}).first
        if !temp.nil?
          temp.destroy
        end
      end
      @comments = @record.comments.order(:created_at).reverse_order.includes(:user)
      @likers = @record.likers
      respond_with(@record)
    end
  end

  def create
    @record = Record.new(record_params)
    if !@record.save
      redirect_to :controller => 'users', :action => 'show', :id => current_user.id, :upload_error => 'true'
    else
      redirect_to :controller => 'users', :action => 'show', :id => current_user.id, :upload_error => 'false'
    end
  end
 
  def increment_view
    @record.update_attribute('view', @record.view + 1)
  end
  
  def increment_like
    if !current_user.nil?
      a = UserLikesRecords.new({:user_id => current_user.id, :record_id => @record.id})
      if !a.save
        @error = "You already liked this audio."
      else
        @record.update_attribute('like', @record.like + 1)
        if @record.user_id != current_user.id
          Notification.create({:user_id => @record.user_id, :category => 0, :who_id => current_user.id, :what_id => @record.id})
        end
        u = @record.user
        u.update_attribute('fame', u.fame + 1)
      end
    else 
      @error = 'You need to log in.'
    end
  end
  
  # for the main page, just show n recent comments
  def short_comment_list
    @comments = @record.comments.order(:created_at).reverse_order.first(15)
  end
  
  def search
    if params[:tag].nil? and params[:more].nil? and params[:query].nil?
      redirect_to '/'
    elsif !params[:tag].nil?
      session[:records] =  Record.tagged_with(ActsAsTaggableOn::Tag.find(params[:tag]).name).where(private:0).collect(&:id)
    elsif !params[:more].nil?
      @offset = params[:more].to_i 
    elsif params[:query].include?('delete') or params[:query].include?('drop') or params[:query].include?('table')
      session[:records] = []
    elsif !params[:query].nil?
      by_tags =  Record.tagged_with(params[:query]).where(private:0).collect(&:id)
      by_title =  Record.where("title ilike ?", "%" + params[:query] + "%").where(private: 0).collect(&:id)
      session[:records] = (by_tags + by_title).uniq
      @user = User.where(nick_name:params[:query])
    end
  end

  private

    def set_record
      @record = Record.find(params[:id])
    end

    def record_params
      params.require(:record).permit(:user_id, :title, :file, :description, :category_id, :private, :tag_list)
    end

    def update_record_params
      params.require(:record).permit(:title, :description, :category_id, :private, :tag_list)
    end
end
