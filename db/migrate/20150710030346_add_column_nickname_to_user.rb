class AddColumnNicknameToUser < ActiveRecord::Migration
  def change
  	add_column :users, :nick_name, :string
  end
end
