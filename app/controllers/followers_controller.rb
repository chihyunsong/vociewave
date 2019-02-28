class FollowersController < ApplicationController
  before_action :authenticate_user!, except: [:get_followers, :get_followings]
  
  respond_to :html
  def follow
     if !current_user.nil?
        u = User.find(params[:id])
        if !u.nil?
          a = Follower.new({:follower_id => current_user.id, :followed_id => u.id})
          if !a.save
            @error = "Something wrong"
          else
            if u.id != current_user.id
              Notification.create({:user_id => u.id, :category => 2, :who_id => current_user.id, :what_id => nil})
            end
            u.update_attribute('fame', u.fame + 5)
            @userid = u.id
          end

        else
          @error = 'Follwing user does not exists.'
        end
    else 
      @error = 'You need to log in.'
    end
  end

  def unfollow
   if !current_user.nil?
        u = User.find(params[:id])
        if !u.nil?
          a = Follower.find_by(follower_id: current_user.id, followed_id:u.id)

          if a.nil?
            @error = "You are not following him"
        
          else
           a.destroy
           u.update_attribute('fame', u.fame - 5)
           @userid = u.id
          end
        else
          @error = "User does not exists"
        end
    else 
      @error = 'You need to log in.'
    end
  end

  def get_followers
    @type = 'followers'
    @user = User.find(params[:user_id])
    @users = @user.followers
  end

  def get_followings
    @type = 'followings'
    @user = User.find(params[:user_id])
    @users = @user.followings
  end

  private
    def set_follower
      @follower = Follower.find(params[:id])
    end

    def follower_params
      params[:follower]
    end
end
