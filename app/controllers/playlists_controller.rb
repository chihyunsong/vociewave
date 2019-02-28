class PlaylistsController < ApplicationController
  before_action :set_playlist, only: [:update, :edit, :show, :increment_like]
  before_action :authenticate_user!, only:[:update, :edit, :get_picture_form, :get_form, :create_playlist, :add_to_playlists, :increment_like]
  respond_to :html,:js  
  
  def edit
    if @playlist.user_id == current_user.id
    else
      redirect_to '/'
    end
  end

  def update
    if current_user.id != @playlist.user_id
      redirect_to '/'
    else
      @playlist.update(update_playlist_params)
      @playlist.record_ids = []
      params[:playlist][:record_ids].each do |r|
        @playlist.record_ids << r
      end

      @playlist.save
      redirect_to :controller => 'users', :action => 'show', :id => current_user.id
    end
  end

  def update_playlist_pic
    @playlist = Playlist.find(params[:playlist_id])
    if @playlist.user_id == current_user.id
      if Playlist.file_extension_check(params[:playlist][:pic].original_filename)
        
        @playlist.update_attribute(:pic, params[:playlist][:pic])
        @user = current_user;
        respond_with(@playlist)
      else
        flash[:alert] = "Not an image file."
        redirect_to '/users/' + current_user.id.to_s
      end
    else
      redirect_to '/'
    end
  end

  def get_picture_form 
    @playlist = Playlist.find(params[:id])
      if @playlist.nil?
        @error ="no way!"
    
      else
        if @playlist.user_id != current_user.id
          @error = "no1"
        else
        end
      end
      respond_with(@playlist)
  end

  def get_form
     @record = Record.find(params[:id])
     if @record.nil?
      @error = "no1"
     else
       @playlists = Playlist.where(user_id: current_user.id)
     end
  end

  def add_to_playlists
    @record = Record.find(params[:record])
    if @record.nil?
      @error = "1"
    else
      if params[:checked].nil?
        @error = "2"
      else
        if params[:checked].empty?
          @error = "2"
        else
          Playlist.find(params[:checked]).each do|p|
            if p.user_id === current_user.id
              #Only playlist's owner is current_user
              k = p.record_ids
              k.push(@record.id)
              p.update(record_ids: k)
              #Update records in playlist
            end
          end
        end
      end
    end

    respond_with(@playlists)
        

  end
  
  def increment_like
    if !current_user.nil?
      a = UserLikesPlaylists.new({:user_id => current_user.id, :playlist_id => @playlist.id})
      if !a.save
        @error = "You already liked this Playlist."
      else
        @playlist.update_attribute('like', @playlist.like + 1)
        u = @playlist.user
        u.update_attribute('fame', u.fame + 1)
      end
    else 
      @error = 'You need to log in.'
    end
  end

  def create_playlist
    if !params[:private].nil?
      pri = true
    else
      pri = false
    end
    @self = true
    
    @playlist = Playlist.new({:user_id => current_user.id, :title => params[:title],:private => pri, :created_at => Time.now})
    if @playlist.save
    else
      @error = '1'
    end
    if !params[:from].nil?
      @from = 'users_show'
    end
    respond_with(@playlist)
  end

  # GET /playlists/1
  # GET /playlists/1.json
  def show
    @user = User.find(@playlist.user_id)
    @playlist.update_attribute('view', @playlist.view + 1)
    @self = false
    if !current_user.nil? and @user.id == current_user.id
      @self = true
    end
    if @playlist.private
      if current_user.nil? || @playlist.user_id != current_user.id
        redirect_to '/'
      end
    end
  end
  
  private
    # Use callbacks to share common setup or constraints between actions.
    def set_playlist
      @playlist = Playlist.find(params[:id])
    end

    # Never trust parameters from the scary internet, only allow the white list through.
    def playlist_params
      params[:playlist]
    end

    def update_playlist_params
      params.require(:playlist).permit(:title, :private, :record_ids)
    end
end
