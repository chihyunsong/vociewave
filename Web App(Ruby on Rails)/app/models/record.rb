class Record < ActiveRecord::Base
  acts_as_taggable
  
  include PgSearch
  pg_search_scope :search_full_text, :against => [:title, :description],
    :using => :tsearch
  
  belongs_to :user
  belongs_to :category
  has_and_belongs_to_many :likers, :class_name => "User", :join_table => "table_user_likes_records", :foreign_key => :record_id
  has_and_belongs_to_many :reporter, :class_name =>"User", :join_table => "reports", :foregin_key => :record_id
  has_many :comments, dependent: :destroy
  has_many :likes, :class_name => 'UserLikesRecords', dependent: :destroy
  has_many :reports, dependent: :destroy
  validates_length_of :title, minimum: 1, maximum: 30
  validates_length_of :description, maximum: 600
  has_attached_file :file, 
                     :path => ":rails_root/public/audios/:id/:basename.:extension",
                     :url => "/audios/:id/:basename.:extension"
  validates :file,
   attachment_content_type: { content_type: [ /.*/ ]}
  # attachment_content_type: { content_type: [ /\Aaudio\/.*\Z/ ]}
  validates_attachment_size :file, :less_than => 27.megabytes
  validate :file_extension_check
  #before_destroy :delete_file

  def not_private
      private == 0
  end


  private 
  
  def file_extension_check
    if file_file_name.ends_with?('.mp3') or file_file_name.ends_with?('.m4a') or self.file_file_name.ends_with?('.wav')
    else
    errors.add(:file_file_name, 'not audio')
    end
  end
  
  def delete_file
  	exec 'rm',  self.file.path.to_s
  end
end
