# encoding: utf-8
class User < ActiveRecord::Base
  include PgSearch
  pg_search_scope :search_full_text, :against => [:nick_name],
    :using => :tsearch
  

  devise :database_authenticatable, :registerable, :confirmable,
         :recoverable, :rememberable, :trackable, :validatable, :omniauthable
  validates_presence_of :password, :on => :create
  validates_uniqueness_of :nick_name, :case_sensitive => false 
  validates_uniqueness_of :email, :case_sensitive => false
  validates :nick_name, presence: true, length: { minimum: 2, maximum: 30 }
  validates :nick_name, :format => { with: /^[\p{Hangul}a-zA-Z0-9]+$/ , :multiline => true, :message => '- 특수문자는 사용하실수 없습니다.' }
  has_many :records, dependent: :destroy
  has_many :comments, dependent: :destroy
  has_many :playlists, dependent: :destroy

  has_and_belongs_to_many :liked_records, :class_name => "Record", :join_table => "table_user_likes_records", :foreign_key => :user_id
  has_and_belongs_to_many :liked_playlists, :class_name => "Playlist", :join_table => "table_user_likes_playlists", :foreign_key => :user_id
  has_and_belongs_to_many :followers, :class_name => "User", :join_table => "followers", :foreign_key => "followed_id", :association_foreign_key => "follower_id"
  has_and_belongs_to_many :followings, :class_name => "User", :join_table => "followers", :foreign_key => "follower_id", :association_foreign_key => "followed_id"
  has_and_belongs_to_many :report_records, :class_name => "Record", :join_table => "reports", :foregin_key => :user_id
  has_attached_file :profile_pic, :styles => { :medium => "300x300>", :thumb => "100x100>" }, :default_url => "Raseone-Record.png"
  validates_attachment_content_type :profile_pic, :content_type => /\Aimage\/.*\Z/
  validates_attachment_size :profile_pic, :less_than => 10.megabytes

  def follows?(user)
  	if Follower.where({:follower_id => self.id, :followed_id => user.id}).length == 1
  		return true
  	end
  	return false
  end

  def self.find_for_facebook_oauth(auth, signed_in_resource=nil)
    user = User.where(:provider => auth.provider, :uid => auth.uid).first
    if user
      return [user]
    else
      registered_user = User.where(:email => auth.info.email).first
      if registered_user
        return [registered_user]
      else
        name = auth.info.name
        hash = ((0...5).map{('1'..'9').to_a[rand(10)]}.join.to_s)
        while(User.where(:nick_name => name).length > 0) do 
          name = auth.info.name + hash
          hash = ((0...5).map{('1'..'9').to_a[rand(10)]}.join.to_s)
        end 
        user = User.new(nick_name:name,
                           provider:auth.provider,
                           uid:auth.uid,
                           email:auth.info.email,
                           password:Devise.friendly_token[0,20])
        user.skip_confirmation! 
        if !user.save
          puts user.errors.messages
          return [false, user]
        end
        return [true, user]
      end
    end
    return [false, user]
  end

  def self.find_for_google_oauth2(auth, signed_in_resource=nil)
    user = User.where(:provider => auth.provider, :uid => auth.uid ).first
    if user
      return [user]
    else
      registered_user = User.where(:email => auth.info.email).first
      if registered_user
        return [registered_user]
      else
        name = auth.info["first_name"] + " " + auth.info["last_name"]
        hash = ((0...5).map{('1'..'9').to_a[rand(10)]}.join.to_s)
        while(User.where(:nick_name => name).length > 0) do 
          name = auth.info.name + hash
          hash = ((0...5).map{('1'..'9').to_a[rand(10)]}.join.to_s)
        end 
        user = User.new(nick_name: name,
                           provider:auth.provider,
                           email:auth.info["email"],
                           uid:auth.uid ,
                           password:Devise.friendly_token[0,20]
        )
        user.skip_confirmation! 
        if !user.save
          puts user.errors.messages
          return [false, user]
        end
        return [true, user]
      end
    end
    return [false, user]
  end

  def generate_api_key
    while true
      token = SecureRandom.base64.tr('+/=', 'Qrt')
      if User.where(api_key: token).length == 0
        break
      end
    end
    self.update_attribute('api_key', token)
    return token
  end

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
