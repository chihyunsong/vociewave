class Api::V1::SessionsController < Api::ApiController
  respond_to :json
  skip_before_action :authenticate, :only => [:create]

  def create
    if params[:type].nil?
      user = User.where("email = ?", params[:email]).first
      if !user or !user.valid_password?(params[:password]) or user.confirmed_at.nil?
        user = nil
      end
    elsif params[:type] == "google"
      response = HTTParty.get("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=#{params[:token]}")
      uid = response["user_id"]
      provider = "google_oauth2"
      user = User.where("email = ?", response["email"]).first
      if user.nil?
        user = User.where("uid= ? AND provider= ?", uid, provider).first
        if user.nil?
          user = User.new(nick_name: (response["email"].split("@")[0]).gsub!(/[^0-9A-Za-z]/, ''),
                             provider: provider,
                             email: response["email"],
                             uid: uid ,
                             password: Devise.friendly_token[0,20])
          user.skip_confirmation!
          if !user.save
            user = nil
          end
        end
      else
        user.update_attributes({:provider=> provider, :uid => uid})
      end
    elsif params[:type] == "facebook"
      response = HTTParty.get("https://graph.facebook.com/me?fields=id,email,name&access_token=#{params[:token]}")
    	response = JSON.parse(response.body)
    	uid = response["uid"]
    	email = response["email"]	
      provider = "facebook"
      user = User.where("email = ?", email).first
      if user.nil?
        user = User.where("uid= ? AND provider= ?", uid, provider).first
        if user.nil?
          user = User.new(nick_name: (email.split("@")[0]).gsub!(/[^0-9A-Za-z]/, ''),
                             provider: provider,
                             email: email,
                             uid: uid ,
                             password: Devise.friendly_token[0,20])
          user.skip_confirmation!
          if !user.save
            user = nil
          end
        end
      else
        user.update_attributes({:provider => provider, :uid =>uid})  
      end
    end

    if user.nil?
      response = { :message => "User not found or error occurred." }
      render json: response.to_json, status: :unauthorized
    else
      api_key = user.generate_api_key  
      response = { :message => "Authentication success", :data => {:user_id => user.id, :key => api_key} }
      render json: response.to_json, status: 200
    end
  end
end
