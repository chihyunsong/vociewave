class CategoriesController < ApplicationController
  before_action :authenticate_user!
  before_action :set_category, only: [:show, :edit, :update, :destroy]

  respond_to :html


  private
    def set_category
      @category = Category.find(params[:id])
    end

    def category_params
      params[:category]
    end
end
