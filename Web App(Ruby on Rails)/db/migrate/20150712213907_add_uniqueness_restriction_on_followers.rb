class AddUniquenessRestrictionOnFollowers < ActiveRecord::Migration
  def change
  	add_index(:followers, [:followed_id,:follower_id], :unique =>true)
  end
end
