<?php



namespace api\modules\v1\models;

use \yii\db\ActiveRecord;



/**

 * This is the model class for table "product_categories".

 *

 * @property integer $id

 * @property string $name

 * @property string $created_date

 * @property string $modified_date

 *

 * @property Products[] $products

 */

class ProductCategories extends ActiveRecord

{

    /**

     * @inheritdoc

     */

    public static function tableName()

    {

        return 'product_categories';

    }



    /**

     * @inheritdoc

     */

    public function rules()

    {

        return [

            [['name', 'created_date','description'], 'required'],

            [['description'], 'string'],

            [['created_date', 'modified_date'], 'safe'],

            [['name'], 'string', 'max' => 50],
            
            [['mark_as_delete'], 'number'],

            [['name'], 'unique','message'=>'Category Name {value} has already been taken'],

        ];

    }



    /**

     * @inheritdoc

     */

    public function attributeLabels()

    {

        return [

            'id' => 'ID',

            'name' => 'Name',

            'created_date' => 'Created Date',

            'modified_date' => 'Modified Date',

        ];

    }



    /**

     * @return \yii\db\ActiveQuery

     */

    public function getProducts()

    {

        return $this->hasMany(Products::className(), ['category_id' => 'id']);

    }

}

