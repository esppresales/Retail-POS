<?php

namespace app\models;

use Yii;

/**
 * This is the model class for table "orders".
 *
 * @property integer $id
 * @property integer $customer_id
 * @property string $customer_name
 * @property integer $employee_id
 * @property string $status
 * @property string $gross_amount
 * @property string $discount_percentage
 * @property string $discount_amount
 * @property string $coupon_code
 * @property string $coupon_discount_amount
 * @property string $tax_percentage
 * @property string $tax_amount
 * @property string $final_amount
 * @property string $created_date
 * @property string $modified_date
 *
 * @property CustomerRedeemHistory[] $customerRedeemHistories
 * @property LoyaltyPointsHistory[] $loyaltyPointsHistories
 * @property OrderItems[] $orderItems
 * @property Customers $customer
 * @property Users $employee
 */
class Orders extends \yii\db\ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'orders';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['customer_id', 'customer_name', 'employee_id', 'status', 'gross_amount', 'tax_percentage', 'tax_amount', 'final_amount', 'created_date'], 'required'],
            [['customer_id', 'employee_id'], 'integer'],
            [['status'], 'string'],
            [['gross_amount', 'discount_percentage', 'discount_amount', 'coupon_discount_amount', 'tax_percentage', 'tax_amount', 'final_amount'], 'number'],
            [['created_date', 'modified_date'], 'safe'],
            [['customer_name'], 'string', 'max' => 100],
            [['coupon_code'], 'string', 'max' => 20]
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
            'customer_name' => 'Customer Name',
            'employee_id' => 'Employee ID',
            'status' => 'Status',
            'gross_amount' => 'Gross Amount',
            'discount_percentage' => 'Discount Percentage',
            'discount_amount' => 'Discount Amount',
            'coupon_code' => 'Coupon Code',
            'coupon_discount_amount' => 'Coupon Discount Amount',
            'tax_percentage' => 'Tax Percentage',
            'tax_amount' => 'Tax Amount',
            'final_amount' => 'Final Amount',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
        ];
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getCustomerRedeemHistories()
    {
        return $this->hasMany(CustomerRedeemHistory::className(), ['order_id' => 'id']);
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getLoyaltyPointsHistories()
    {
        return $this->hasMany(LoyaltyPointsHistory::className(), ['order_id' => 'id']);
    }

    /**
     * @return \yii\db\ActiveQuery
     */
    public function getOrderItems()
    {
        return $this->hasMany(OrderItems::className(), ['order_id' => 'id']);
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
    public function getEmployee()
    {
        return $this->hasOne(Users::className(), ['id' => 'employee_id']);
    }
}
