class RemoveDescriptionPlaylist < ActiveRecord::Migration
  def change
  	remove_column :playlists, :description
  end
end
