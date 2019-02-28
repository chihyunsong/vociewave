class AddDefaultValueToPrivate < ActiveRecord::Migration
  def change
  	change_column :users, :admin, :boolean, :default => false
  	change_column :playlists, :private, :boolean, :default => false
  end

end
