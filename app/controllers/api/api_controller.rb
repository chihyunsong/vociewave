class Api::ApiController < ActionController::Base
	respond_to :json
	 private
 
	def authenticate
		@user = User.where(api_key: params[:token]).first
		if @user.nil? or @user.id != params[:user_id].to_i
			response = { :message => "Not authenticated" }
        	render json: response.to_json, status: 400
		end
	end
end
