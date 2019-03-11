class CommentsController < ApplicationController
  before_action :authenticate_user!
  
  respond_to :html

  def create
    if current_user.nil?
      @error = "You need to log in."
    else
      @record = Record.find(params[:record_id])
      if @record.nil?
        @error = "Record does not exist."
      else
        @comment = Comment.new({:user_id => current_user.id, :record_id => params[:record_id], :description => params[:description], :created_at => Time.now})
        if !@comment.save
          @error = "Internal error occurred."
        else
          if @record.user_id != current_user.id        
            Notification.create({:user_id => @record.user_id, :category => 1, :who_id => current_user.id, :what_id => @record.id})
          end
        end
      end
    end
  end

  private
    def set_comment
      @comment = Comment.find(params[:id])
    end

    def comment_params
      params.require(:comment).permit(:user_id, :description, :record_id, :created_at, :updated_at)
    end
end
