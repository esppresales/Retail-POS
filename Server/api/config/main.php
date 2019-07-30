<?php

$params = array_merge(
    require(__DIR__ . '/../../common/config/params.php'),
    require(__DIR__ . '/../../common/config/params-local.php'),
    require(__DIR__ . '/params.php'),
    require(__DIR__ . '/params-local.php')
);

return [
    'id' => 'app-api',
    'basePath' => dirname(__DIR__),    
    'bootstrap' => ['log'],
    'modules' => [
        'v1' => [
            'basePath' => '@app/modules/v1',
            'class' => 'api\modules\v1\Module'
        ]
    ],
    'components' => [        
        'user' => [
            'identityClass' => 'common\models\User',
            'enableAutoLogin' => true,
            'loginUrl' => ''
        ],
        'request' => [
            'enableCookieValidation' => true,
            'enableCsrfValidation' => true,
            'cookieValidationKey' => 'xxxxxxx',
        ],
        'common_functions' => [
//            'basePath' => '@app/components/',                
            'class' => 'api\components\CommonFunctions'
                    ],
//        'users' => [
//            'class' => 'common\models\Users',
//            'enableAutoLogin' => true,
//            'loginUrl' => ''
//        ],
        'log' => [
            'traceLevel' => YII_DEBUG ? 3 : 0,
            'targets' => [
                [
                    'class' => 'yii\log\FileTarget',
                    'levels' => ['error', 'warning'],
                ],
            ],
        ],
        'urlManager' => [
            'enablePrettyUrl' => true,
            'enableStrictParsing' => true,
            'showScriptName' => false,
            'rules' => [
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/customers',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ]
                    
                ],
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/users',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
                    'patterns' => [
                        'PUT {id}' => 'update', 
                        'DELETE {id}' => 'delete', 
                        'GET,HEAD {id}' => 'view', 
                        'POST' => 'create', 
                        'GET,HEAD' => 'index', 
                        'POST {id}' => 'deactivate',
                        '{id}' => 'options', 
                        '' => 'options',
                    ], 
                    
                ],
//                [
//                    'class' => 'yii\rest\UrlRule', 
//                    'controller' => 'v1/orders',
//                    'tokens' => [
//                        '{id}' => '<id:\\w+>'
//                    ]
//                    
//                ],
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/categories',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
//                    'pluralize' => false,
                    
                ],
                
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/time-tracking',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
                    'pluralize' => false,
                    
                ],
                
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/sales-chart',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
                    'pluralize' => false,
                    
                ],
                
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/product-threshold',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
                    'pluralize' => false,
                    
                ],
                
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/product-performance',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
                    'pluralize' => false,
                    
                ],
                
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/settings',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
//                    'pluralize' => false,
                    
                ],
                
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/membership',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
                    'pluralize' => false,
                    
                ],
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/config',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
                    'pluralize' => false,
                    
                ],
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/countries',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
//                    'pluralize' => false,
                    
                ],
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/dashboard',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
                    'pluralize' => false,
                    
                ],
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/products',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ]
                    
                ],
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/taxes',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ]
                    
                ],
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/discounts-offers',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ]
                    
                ],
//                [
//                    'class' => 'yii\rest\UrlRule', 
//                    'controller' => 'v1/orders',
//                    'extraPatterns' => [
//                        'POST return ' => 'return'
//                    ]
//                    
//                ],
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/orders',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ]
                    
                ],
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/coupons',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ]
                    
                ],
                [
                    'class' => 'yii\rest\UrlRule', 
                    'controller' => 'v1/product-category',
                    'tokens' => [
                        '{id}' => '<id:\\w+>'
                    ],
                    'pluralize' => false,
                    
                ],
//                [
//                    'class' => 'yii\rest\UrlRule', 
//                    'controller' => 'v1/login',
//                    'tokens' => [
//                        '{id}' => '<id:\\w+>'
//                    ]
//                    
//                ],
                [
                'class' => 'yii\rest\UrlRule', 
                'controller' => 'v1/login',
                'patterns' => [
                    'POST' => 'signin', 
//                    'POST' => 'signout',
                    ],
                    'pluralize' => false,
//                'extraPatterns' => [
//                    'GET search' => 'search'
//                    ],                 
            ],
            [
                'class' => 'yii\rest\UrlRule', 
                'controller' => 'v1/logout',
                'patterns' => [
                    'GET' => 'signout', 
//                    'POST' => 'signout',
                    ],
                'pluralize' => false,
//                'extraPatterns' => [
//                    'GET search' => 'search'
//                    ],                 
            ],
            [
                'class' => 'yii\rest\UrlRule', 
                'controller' => 'v1/user',
                'tokens' => [
                    '{id}' => '<id:\\w+>'
                ]

            ],
            [
                'class' => 'yii\rest\UrlRule', 
                'controller' => 'v1/reports',
                'tokens' => [
                    '{id}' => '<id:\\w+>'
                ],
                 'pluralize' => false,

            ],
            [
                'class' => 'yii\rest\UrlRule', 
                'controller' => 'v1/roles',
                'tokens' => [
                    '{id}' => '<id:\\w+>'
                ]

            ],
            ], 
        ]
    ],
    'params' => $params,
];



