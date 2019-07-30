<?php

namespace api\modules\v1\models;
use \yii\db\ActiveRecord;

/**
 * This is the model class for table "loyalty_points_history".
 *
 * @property integer $id
 * @property integer $order_id
 * @property integer $customer_id
 * @property string $amount_spend
 * @property integer $earned_points
 * @property string $created_date
 * @property string $modified_date
 *
 * @property Orders $order
 * @property Customers $customer
 */
class LoyaltyPointsHistory extends ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'loyalty_points_history';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['order_id', 'customer_id', 'amount_spend', 'earned_points', 'created_date'], 'required'],
            [['order_id', 'customer_id', 'earned_points'], 'integer'],
            [['amount_spend'], 'number'],
            [['created_date', 'modified_date'], 'safe']
        ];
    }

    /**
     * @inheritdoc
     */
    public function attributeLabels()
    {
        return [
            'id' => 'ID',
            'order_id' => 'Order ID',
            'customer_id' => 'Customer ID',
            'amount_spend' => 'Amount Spend',
            'earned_points' => 'Earned Points',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
        ];
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getOrder()
    {
        return $this->hasOne(Orders::className(), ['id' => 'order_id']);
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getCustomer()
    {
        return $this->hasOne(Customers::className(), ['id' => 'customer_id']);
    }
}
