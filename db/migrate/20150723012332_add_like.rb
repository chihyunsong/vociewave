class AddLike < ActiveRecord::Migration
  def change
  	add_column :playlists, :like, :integer, null: false, :default => 0
  end
end
