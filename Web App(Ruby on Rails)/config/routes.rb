Rails.application.routes.draw do


  namespace :api do
    namespace :v1 do
      resources :records
      resources :users
      resources :comments
      get 'increment_like', :controller => :records, :action => :increment_like
      get 'increment_view', :controller => :records, :action => :increment_view
      
      post 'sessions' => 'sessions#create', :as => 'login'
      delete 'sessions' => 'sessions#destroy', :as => 'logout'
      get 'main' => 'welcome#main', :as => 'main'
    end
  end

  resources :followers
  get 'follow', :controller => :followers, :action => :follow
  get 'unfollow', :controller => :followers, :action => :unfollow
  get 'get_followers', :controller => :followers, :action => :get_followers
  get 'get_followings', :controller => :followers, :action => :get_followings

  resources :sub_categories

  resources :categories
  resources :playlists
  post 'create_playlist',:controller => :playlists, :action => :create_playlist
  get 'add_to_playlist_form', :controller => :playlists, :action => :get_form
  get 'picture_form', :controller => :playlists, :action => :get_picture_form
  post 'add_to_playlists', :controller => :playlists, :action => :add_to_playlists
  get 'increment_view_playlist', :controller => :playlists, :action => :increment_view
  get 'increment_like_playlist', :controller => :playlists, :action => :increment_like
  resources :comments
  resources :reports
  get 'reports/:id/ignore', :controller => :reports, :action => :ignore,:as =>"ignore_report"
  resources :records
  get 'increment_view', :controller => :records, :action => :increment_view
  get 'increment_like', :controller => :records, :action => :increment_like
  get 'short_comment_list', :controller => :records, :action => :short_comment_list
  post 'records/save_upload', :controller => :records, :action => :save_upload
  post 'search', :controller => :records, :action => :search
  get 'search', :controller => :records, :action => :search
  post 'report', :controller => :reports, :action => :report

  devise_for :users, :controllers => {:passwords=>"passwords",:registrations=>"registrations",:omniauth_callbacks => "omniauth_callbacks" }

  resources :users
  get "reset_password", :controller => :users, :action => :reset_password
  post "update_profile_pic", :controller => :users, :action => :update_profile_pic
  post 'update_playlist_pic',:controller => :playlists, :action => :update_playlist_pic
  get 'voices_i_liked', :controller => :users, :action => :voices_i_liked
  get 'voices_i_uploaded', :controller => :users, :action => :voices_i_uploaded
  get 'playlists_i_liked', :controller => :users, :action => :playlists_i_liked
  get 'users_show_more' => 'users#users_show_more', :as =>'users_show_more'
  get 'playlists_show_more' => 'users#playlists_show_more', :as =>'playlists_show_more'
  get 'users/:id/playlists', :controller => :users, :action => :playlists, :as =>'user_playlists'
  devise_scope :user do
    get "sign_in", to: "devise/sessions#new"
    get "sign_out", to: "devise/sessions#destroy" 

  end
  get '/about' => 'welcome#about', :as => 'about'
  get '/report_form', :controller => :reports, :action => :get_form
  get 'welcome/index'
  get '/main_category_select' => 'welcome#main_category_select', :as => 'main_category_select'
  get '/myspace' => 'welcome#myspace', :as => 'myspace'
  get '/more' => 'welcome#records_more', :as =>'records_more'

  get 'clear_all_info', :controller => :welcome, :action => :clear_all_info
  # The priority is based upon order of creation: first created -> highest priority.
  # See how all your routes lay out with "rake routes".

  # You can have the root of your site routed with "root"
  root 'welcome#index'

  # Example of regular route:
  #   get 'products/:id' => 'catalog#view'

  # Example of named route that can be invoked with purchase_url(id: product.id)
  #   get 'products/:id/purchase' => 'catalog#purchase', as: :purchase

  # Example resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Example resource route with options:
  #   resources :products do
  #     member do
  #       get 'short'
  #       post 'toggle'
  #     end
  #
  #     collection do
  #       get 'sold'
  #     end
  #   end

  # Example resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Example resource route with more complex sub-resources:
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get 'recent', on: :collection
  #     end
  #   end

  # Example resource route with concerns:
  #   concern :toggleable do
  #     post 'toggle'
  #   end
  #   resources :posts, concerns: :toggleable
  #   resources :photos, concerns: :toggleable

  # Example resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end
end
