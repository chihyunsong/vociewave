class Notification < ActiveRecord::Base

	# catagory 0 => 'who_id' likes 'record_id' whose uploader is 'user_id'
	# category 1 => 'who_id' commented on 'record_id' whose uploader is 'user_id'
	# category 2 => 'who_id' follows 'user_id'
	
end
