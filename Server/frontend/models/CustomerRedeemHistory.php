<?php

namespace app\models;

use Yii;

/**
 * This is the model class for table "customer_redeem_history".
 *
 * @property integer $id
 * @property integer $customer_id
 * @property integer $order_id
 * @property integer $redeem_points
 * @property string $points_amount
 * @property string $created_date
 * @property string $modified_date
 *
 * @property Customers $customer
 * @property Orders $order
 */
class CustomerRedeemHistory extends \yii\db\ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'customer_redeem_history';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['customer_id', 'order_id', 'redeem_points', 'points_amount', 'created_date'], 'required'],
            [['customer_id', 'order_id', 'redeem_points'], 'integer'],
            [['points_amount'], 'number'],
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
            'customer_id' => 'Customer ID',
            'order_id' => 'Order ID',
            'redeem_points' => 'Redeem Points',
            'points_amount' => 'Points Amount',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
        ];
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getCustomer()
    {
        return $this->hasOne(Customers::className(), ['id' => 'customer_id']);
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getOrder()
    {
        return $this->hasOne(Orders::className(), ['id' => 'order_id']);
    }
}
