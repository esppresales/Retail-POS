<?php

namespace api\modules\v1\models;
use \yii\db\ActiveRecord;

/**
 * This is the model class for table "products".
 *
 * @property integer $id
 * @property string $name
 * @property string $description
 * @property string $barcode
 * @property string $cost_price
 * @property integer $quantity
 * @property integer $category_id
 * @property string $image
 * @property string $supplier
 * @property integer $min_stock_alert_qty
 * @property string $stock_location
 * @property string $created_date
 * @property integer $added_by
 * @property string $modified_date
 *
 * @property OrderItems[] $orderItems
 * @property Users $addedBy
 * @property ProductCategories $category
 */
class Products extends \yii\db\ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'products';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['name', 'description', 'barcode', 'cost_price', 'quantity', 'category_id', 'min_stock_alert_qty', 'stock_location', 'created_date', 'added_by'], 'required'],
            [['description'], 'string'],
            [['cost_price','selling_price'], 'number'],
            [['quantity', 'category_id', 'min_stock_alert_qty', 'added_by'], 'integer'],
            [['created_date', 'modified_date'], 'safe'],
            [['name', 'barcode', 'image', 'supplier', 'stock_location'], 'string', 'max' => 100],
	    //[['barcode'], 'unique']
            [['barcode'], 'custom_unique_function'] // added by Divya for barcode issue
        ];
    }
    // Code mofidied by Divya for barcode unique
    public function custom_unique_function($attribute, $params) {
        $value = $this->$attribute;
        $models = Products::find()
                ->all();
        foreach($models as $model ){
            // modified by Divya to resolve issue of barcode
            if($model->barcode == $value && $model->mark_as_delete == 0 && $model->id!=$this->id){                       
                $this->addError('barcode', \Yii::t('app', "Barcode '".$this->barcode."' has already been taken."));
                break;
            }
        }
    }

    /**
     * @inheritdoc
     */
    public function attributeLabels()
    {
        return [
            'id' => 'ID',
            'name' => 'Name',
            'description' => 'Description',
            'barcode' => 'Barcode',
            'cost_price' => 'Cost Price',
            'quantity' => 'Quantity',
            'category_id' => 'Category ID',
            'image' => 'Image',
            'supplier' => 'Supplier',
            'min_stock_alert_qty' => 'Min Stock Alert Qty',
            'stock_location' => 'Stock Location',
            'created_date' => 'Created Date',
            'added_by' => 'Added By',
            'modified_date' => 'Modified Date',
        ];
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getOrderItems()
    {
        return $this->hasMany(OrderItems::className(), ['product_id' => 'id']);
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getAddedBy()
    {
        return $this->hasOne(Users::className(), ['id' => 'added_by']);
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getCategory()
    {
        return $this->hasOne(ProductCategories::className(), ['id' => 'category_id']);
    }
}
