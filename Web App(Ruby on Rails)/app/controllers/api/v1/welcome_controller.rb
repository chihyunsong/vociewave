class Api::V1::WelcomeController < Api::ApiController
  respond_to :json
  require 'uri'
  #before_action :authenticate

  def main
    if !params[:category].nil?
      if params[:category] == 'all'
        records = Record.where(private:0)
      else
        records = Record.where(private:0).where(category_id:params[:category])
      end

      if !params[:recent].nil? and params[:recent] == 'true'
        records = records.order(:created_at).reverse_order.includes(:user).first(200)
      else
        if params[:popularity] == 'day'
          records = records.order(:like).reverse_order.where("created_at >= '#{(Time.now - 1.day).utc.iso8601}'")
        elsif params[:popularity] == 'week'
          records = records.order(:like).reverse_order.where("created_at >= '#{(Time.now - 1.week).utc.iso8601}'")
        else
          records = records.order(:like).reverse_order
        end
      end
      @records = []
      records.each do |r|
        @records << {:id => r.id, :user_id => r.user_id, :user_nick_name => r.user.nick_name, :title => r.title, :path => r.file.url, :user_profile_path => r.user.profile_pic.url(:thumb)}
      end
      response = { :data => @records }
      render json: response.to_json, status: 200
    else
      response = { :message => "invalid request" }
      render json: response.to_json, status: 400
    end
  end

  private
    def record_params
      params.permit(:user_id, :title, :file, :description, :category_id, :private)
    end
end