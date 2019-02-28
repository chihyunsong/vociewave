class AddPrivateToPlaylist < ActiveRecord::Migration
  def change
  	add_column :playlists, :private, :boolean
  end
end
