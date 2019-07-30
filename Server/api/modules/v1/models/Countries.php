<?php

namespace api\modules\v1\models;
use \yii\db\ActiveRecord;

/**
 * This is the model class for table "countries".
 *
 * @property integer $id
 * @property string $country_name
 * @property string $country_capital
 */
class Countries extends ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'countries';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['country_name', 'country_capital'], 'string', 'max' => 40]
        ];
    }

    /**
     * @inheritdoc
     */
    public function attributeLabels()
    {
        return [
            'id' => 'ID',
            'country_name' => 'Country Name',
            'country_capital' => 'Country Capital',
        ];
    }
}
