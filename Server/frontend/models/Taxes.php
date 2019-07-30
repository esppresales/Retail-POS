<?php

namespace app\models;

use Yii;

/**
 * This is the model class for table "taxes".
 *
 * @property integer $id
 * @property string $percentage
 * @property string $type
 * @property string $created_date
 * @property string $modified_date
 */
class Taxes extends \yii\db\ActiveRecord
{
    /**
     * @inheritdoc
     */
    public static function tableName()
    {
        return 'taxes';
    }

    /**
     * @inheritdoc
     */
    public function rules()
    {
        return [
            [['percentage', 'type', 'created_date'], 'required'],
            [['percentage'], 'number'],
            [['created_date', 'modified_date'], 'safe'],
            [['type'], 'string', 'max' => 20]
        ];
    }

    /**
     * @inheritdoc
     */
    public function attributeLabels()
    {
        return [
            'id' => 'ID',
            'percentage' => 'Percentage',
            'type' => 'Type',
            'created_date' => 'Created Date',
            'modified_date' => 'Modified Date',
        ];
    }
}
