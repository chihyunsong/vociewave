class UserLikesPlaylists < ActiveRecord::Base
	self.table_name = 'table_user_likes_playlists'
	belongs_to :users
	belongs_to :playlists
	validates_uniqueness_of :user_id, :scope => [:playlist_id] # for uniq pair
end