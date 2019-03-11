# 1: 저작권 2:음란물 3: 욕설 100: 기타

class Report < ActiveRecord::Base
	belongs_to :users
	belongs_to :records
	validates_uniqueness_of :user_id, :scope => [:record_id] # for uniq pair
	validates_length_of :content, minimum: 10
end
