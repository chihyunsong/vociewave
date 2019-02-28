class WelcomeController < ApplicationController

  before_action :authenticate_user!, only: [:myspace, :clear_all_info]
  def index
    @sort_by = 'time'
    if !params[:omniauth_error].nil?
      @omniauth_error = false
      if params[:omniauth_error] == 'true'
        @omniauth_error = true
      end
    end 
  	@categories = Category.all

    if !current_user.nil?
      @selected_category_id = 'news_feed'
      following_ids = current_user.followings.collect(&:id)
      session[:records] = Record.where(private:0).order(:created_at).reverse_order.includes(:user).where(user_id:following_ids.uniq).collect(&:id)
    end  
  	
  	if current_user.nil? or session[:records].length == 0
      @selected_category_id = 'all'
      session[:records] = Record.order(:created_at).reverse_order.includes(:user).where(private: 0).first(200).collect(&:id)
    end

    @top_users = User.order(:fame).reverse_order.first(7)
    @following_users = []
    if !current_user.nil?
      @following_users = current_user.followings.collect(&:id);
    end
    if !current_user.nil?
      temp = []
      Notification.where(:user_id => current_user.id).order(:created_at).reverse_order.each do |n|
        temp << [n.user_id, n.category, n.who_id, n.what_id, n.id]
      end
      session[:notification] = temp
    end
    @tags = Record.tag_counts_on(:tags).first(10)
  end
  
  def myspace
    @user = current_user
    @record = Record.new
  end

  def records_more
    @offset = params[:id].to_i     
  end

  def about  
  end

  def main_category_select    
    # figure out 'sort_by': time or popularity, default -> popularity
    @sort_by = 'time'
    if !params[:sort_by].nil?
      if params[:sort_by] == 'popularity_today'
        @sort_by = 'popularity_today'
      elsif params[:sort_by] == 'popularity_week'
        @sort_by = 'popularity_week'
      elsif params[:sort_by] == 'popularity_all'
        @sort_by = 'popularity_all'
      end
    end

    # figure out 'category': default -> null
  	if params[:id] == 'all'
      if @sort_by == 'popularity_all'
  		  session[:records] = Record.order(:like).reverse_order.includes(:user).where(private:0).first(200).collect(&:id);
  		elsif @sort_by == 'popularity_today'
        session[:records] = Record.order(:like).reverse_order.includes(:user).where("created_at >= '#{(Time.now - 1.day).utc.iso8601}'").where(private:0).first(200).collect(&:id);
      elsif @sort_By == 'popularity_week'
        session[:records] = Record.order(:like).reverse_order.includes(:user).where("created_at >= '#{(Time.now - 7.day).utc.iso8601}'").where(private:0).first(200).collect(&:id); 
      else
        session[:records] = Record.order(:created_at).reverse_order.includes(:user).where(private:0).first(200).collect(&:id);
      end
      @selected_category_id = 'all'
    elsif params[:id] == 'news_feed'
      following_ids = current_user.followings.collect(&:id)
      if @sort_by == 'popularity_all'
        session[:records] = Record.where(private:0).order(:like).reverse_order.includes(:user).where(user_id:following_ids.uniq).collect(&:id);
      elsif @sort_by == 'popularity_today'
        session[:records] = Record.where(private:0).order(:like).reverse_order.includes(:user).where("created_at >= '#{(Time.now - 1.day).utc.iso8601}'").where(user_id:following_ids.uniq).collect(&:id);
      elsif @sort_by == 'popularity_week'
        session[:records] = Record.where(private:0).order(:like).reverse_order.includes(:user).where("created_at >= '#{(Time.now - 7.day).utc.iso8601}'").where(user_id:following_ids.uniq).collect(&:id);
      else
        session[:records] = Record.where(private:0).order(:created_at).reverse_order.includes(:user).where(user_id:following_ids.uniq).collect(&:id);
      end
      @selected_category_id = 'news_feed'
  	else
	  	@category = Category.find(params[:id])
	  	if @category.nil?
	  		@error = "Category does not exist."
	  	else
        if @sort_by == 'popularity_all'
          session[:records] = @category.records.order(:like).reverse_order.where(private:0).first(200).collect(&:id);
		  	elsif @sort_by == 'popularity_today'
          session[:records] = @category.records.order(:like).reverse_order.where("created_at >= '#{(Time.now - 1.day).utc.iso8601}'").where(private:0).first(200).collect(&:id);
        elsif @sort_by == 'popularity_week'
          session[:records] = @category.records.order(:like).reverse_order.where("created_at >= '#{(Time.now - 7.day).utc.iso8601}'").where(private:0).first(200).collect(&:id);    
        else
          session[:records] = @category.records.order(:created_at).reverse_order.where(private:0).first(200).collect(&:id);
        end
        @selected_category_id = params[:id]
		  end
	   end
  end

  def clear_all_info
    session[:notification] = nil
    Notification.where(user_id:current_user.id).destroy_all
  end
end
