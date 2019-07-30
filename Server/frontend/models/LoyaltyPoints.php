<?php

namespace app\models;

use Yii;

/**
 * This is the model class for table "loyalty_points".
 *
 * @property integer $id
 * @property integer $is_loyalty_on
 * @property string $earned_amount
 * @property string $created_date
 * @property string $modified_date
 */
class LoyaltyPoints extends \yii\db\ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'loyalty_points';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['is_loyalty_on', 'created_date', 'modified_date'], 'required'],
            [['is_loyalty_on'], 'integer'],
            [['earned_amount'], 'number'],
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
            'is_loyalty_on' => 'Is Loyalty On',
            'earned_amount' => 'Earned Amount',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
        ];
    }
}
