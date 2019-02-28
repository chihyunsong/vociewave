class AddPicToPlaylist < ActiveRecord::Migration
  def change


  	add_attachment :playlists, :pic

	end
end
