class UserLikesRecords < ActiveRecord::Base
	self.table_name = 'table_user_likes_records'
	belongs_to :users
	belongs_to :records
	validates_uniqueness_of :user_id, :scope => [:record_id] # for uniq pair

end
