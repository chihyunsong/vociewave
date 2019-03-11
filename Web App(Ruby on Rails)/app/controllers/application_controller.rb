class ApplicationController < ActionController::Base
  # Prevent CSRF attacks by raising an exception.
  # For APIs, you may want to use :null_session instead.
  protect_from_forgery with: :exception
  before_filter :update_sanitized_params, if: :devise_controller?
    def update_sanitized_params
	  devise_parameter_sanitizer.for(:sign_up) {|u| u.permit(:email, :nick_name, :password, :password_confirmation,:description)}
	  devise_parameter_sanitizer.for(:account_update) { |u| u.permit(:current_password, :email, :nick_name, :password, :password_confirmation,:description) }
	end
	
	def user_admin!
      unless !current_user.admin.nil? && current_user.admin == true
        render 'my_js'
      end
    end

	def authenticate_user!(options={})
	  unless current_user
	    j render 'my_js'

	  end
	  if !current_user.nil?
		  temp = []
		  Notification.where(:user_id => current_user.id).order(:created_at).reverse_order.each do |n|
		  	temp << [n.user_id, n.category, n.who_id, n.what_id, n.id]
		  end
		  session[:notification] = temp
		end
	end

	def check_self(user_id)
		if current_user and current_user.id == user_id
			return true
		else
			return false
		end
	end
end
