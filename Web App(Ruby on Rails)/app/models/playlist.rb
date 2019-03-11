class Playlist < ActiveRecord::Base
	belongs_to :user
	validates_length_of :title, minimum: 2, maximum: 30
	has_attached_file :pic, :styles => { :medium => "300x300>", :thumb => "100x100>" }, :default_url => ""
  	validates_attachment_content_type :pic, :content_type => /\Aimage\/.*\Z/
  	validates_attachment_size :pic, :less_than => 10.megabytes
  	has_and_belongs_to_many :likers, :class_name => "User", :join_table => "table_user_likes_playlists", :foreign_key => :record_id
  	
  	private 
  
	  def self.file_extension_check(file_name)
	    name = file_name.downcase
	    if name.ends_with?('.jpg') or name.ends_with?('.png') or name.ends_with?('.gif') or name.ends_with?('.bmp') or name.ends_with?('.jpeg')
	      return true
	    else
	      return false
	    end
	  end
end

