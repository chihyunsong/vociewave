class OmniauthCallbacksController < Devise::OmniauthCallbacksController
  
  def facebook 
    @user = User.find_for_facebook_oauth(request.env["omniauth.auth"], current_user)
    if @user[0] == false 
      redirect_to :controller => 'welcome', :action => 'index', :omniauth_error => 'true'
    elsif @user[0] == true
      sign_in(:user, @user[1])
      redirect_to :controller => 'welcome', :action => 'index', :omniauth_error => 'false'
    elsif @user[0].persisted?
      sign_in_and_redirect @user[0], :event => :authentication
      set_flash_message(:notice, :success, :kind => "Facebook") if is_navigational_format?
    else
      session["devise.facebook_data"] = request.env["omniauth.auth"]
      redirect_to new_user_registration_url
    end
  end

  def google_oauth2
    @user = User.find_for_google_oauth2(request.env["omniauth.auth"], current_user)
    if @user[0] == false 
      redirect_to :controller => 'welcome', :action => 'index', :omniauth_error => 'true'
    elsif @user[0] == true
      sign_in(:user, @user[1])
      redirect_to :controller => 'welcome', :action => 'index', :omniauth_error => 'false'
    elsif @user[0].persisted?
      sign_in_and_redirect @user[0], :event => :authentication
      set_flash_message(:notice, :success, :kind => "Google") if is_navigational_format?
    else
      session["devise.google_data"] = request.env["omniauth.auth"]
      redirect_to new_user_registration_url
    end
  end
end