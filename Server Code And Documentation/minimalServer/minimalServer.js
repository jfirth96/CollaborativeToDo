
var express    = require('express');
var app        = express();
var bodyParser = require('body-parser');

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var port = process.env.PORT || 8080;        // set port
var router = express.Router();           // get instance of express Router


const mysql = require('mysql');
// const 
let connection = null;

let hostname = '104.198.199.129'; //104.198.199.129
let userName = 'root';
var password = "auburn56";//process.argv[2];
let dbName = 'userData';


router.use(function(req, res, next) {
    // do logging
    console.log('Open connection...');

    connection = mysql.createConnection({
      host     : hostname,
      user     : userName,
      password : password,
      database : dbName
    });

    connection.connect();

    next();
});

app.use('/api', router);

function checkAuth(userName, userKey){
    let authCheckString = "select count(*) as authCheck from users where userName like \'" + userName + "\' and userKey like \'" + userKey + "\');";
    connection.query(authCheckString, function(err, rows, fields) {
        if (err) return 0;
        let checkVal = rows[0].authCheck;

        if(checkVal === 1) {
            return 1;
        }
        else {
            return 0;
        }
    });
}

router.route('/testEndpoint').get(function(req,res) {
    res.json({success:true, message: "The server is live, This is a generic response"});
});

router.route('/paramEndpoint').post(function(req,res) {
    let test1 = req.body.test1;
    let test2 = req.body.test2;
    
    res.json({param1: test1, param2:test2});
});

router.route('/seeallusers').get(function(req,res) {
    
    let testQuery = "select * from users;";

    connection.query(testQuery, function(err, rows, fields) {
        if (err) {res.json({success:false, data: []})};
        
        size = rows.length;
        let returnObj = [];

        for(i = 0; i < size; i++)
        {
            returnObj.push(rows[i]);
            console.log(rows[i]);
        }
        res.json({success:true, data: returnObj});
    });
});

router.route('/seeallboards').get(function(req,res) {
    
    let testQuery = "select * from boards;";

    connection.query(testQuery, function(err, rows, fields) {
        if (err) {res.json({success:false, data: []})};
        
        size = rows.length;
        let returnObj = [];

        for(i = 0; i < size; i++)
        {
            returnObj.push(rows[i]);
            console.log(rows[i]);
        }
        res.json({success:true, data: returnObj});
    });
});

router.route('/seealltasks').get(function(req,res) {
    
    let testQuery = "select * from tasks;";

    connection.query(testQuery, function(err, rows, fields) {
        if (err) {res.json({success:false, data: []})};
        
        size = rows.length;
        let returnObj = [];

        for(i = 0; i < size; i++)
        {
            returnObj.push(rows[i]);
            console.log(rows[i]);
        }
        res.json({success:true, data: returnObj});
    });
});

router.route('/seeallmembers').get(function(req,res) {
    
    let testQuery = "select * from memberof;";

    connection.query(testQuery, function(err, rows, fields) {
        if (err) {res.json({success:false, data: []})};
        
        size = rows.length;
        let returnObj = [];

        for(i = 0; i < size; i++)
        {
            returnObj.push(rows[i]);
            console.log(rows[i]);
        }
        res.json({success:true, data: returnObj});
    });
});

router.route('/authUser').post(function(req, res) { //internal function for verifying a valid username and password 

    let userName = req.body.userName;
    let key = req.body.key;
    let result = false;

    let testQuery = "select count(*) as verify from users where userName like \'" + userName + "\' and userKey like \'" + key + "\';";
    console.log(testQuery);
    let verifyVal = 0;
    connection.query(testQuery, function (err, rows, fields) {
        if (err) res.json({ result: false });
        verifyVal = rows[0].verify;
        console.log(verifyVal);

        if (verifyVal === 1){
            result = true;
            let findUserID = "select userID from users where userName like \'" + userName + "\';";
            connection.query(findUserID, function(err, rows, fields){
                if(err) res.json({result:false});

                if(rows.length > 0)
                {
                    let userID = rows[0].userID;
                    res.json({success:true, userID:userID})
                }
                else {
                    res.json({result:false});
                }
            });

        }
        else {
            result = false;
            res.json({ success : result });
        }
        // res.json({ success : result });
    });
});

//check if username is available
router.route('/isUserNameAvailable').post(function(req,res){ //returns true if the username is not taken, false if it is
    let userName = req.body.userName;
    
    let testQuery = "select count(*) as count from users where userName like \'" + userName + "\';"
    console.log(testQuery)
    connection.query(testQuery, function(err, rows, fields) {
        if (err) res.json({ success: false });
        // console.log(rows)

        verifyVal = rows[0].count;

        if(verifyVal === 0)
        {
            res.json({ success: true });
        }
        else {
            res.json({ success: false });
        }
    });
});

// route: users - post 
// takes userName, key, optional email
// creates user if username is not already taken. Returns error if username is taken
// returns userID value if user is created
router.route('/users').post(function(req, res) { //create user, must have unique username

    let userName = req.body.userName;
    let key = req.body.key;
    let email = req.body.email;

    //check if username exists
    let userExistsQuery = "select count(*) as checkUser from users where userName like \'" + userName + "\';";
    console.log(userExistsQuery);
    let checkUser = 0;
    connection.query(userExistsQuery, function (err, rows, fields) {
        if (err) res.json({ userID: '', success: false });
        checkUser = rows[0].checkUser;
        console.log(checkUser);

        if (checkUser === 0){
            console.log("Username does not exist, creating new user");
            let createUserQuery = "insert into users(userName,userKey,email) values (\'"+userName+"\',\'"+key+"\',\'"+email+"\');"
            console.log(createUserQuery);

            connection.query(createUserQuery, function (err, rows, fields) {
                if (err) res.json({ userID: '', success: false });
                else {
                    console.log("Created user without error");
                    let getInternalIDQuery = "select userID from users where userName like \'" + userName + "\';"
                    connection.query(getInternalIDQuery, function (err, rows, fields) {
                        if (err) res.json({ userID: '', success: false });
                        let internalID = rows[0].userID;
                        res.json({ userID: internalID, success: true });
                    });
                };
            });
        }
        else {
            res.json({ userID: '', success: false });
        }
    });
});


router.route('/completeToDo').post(function(req, res) {
    let userName = req.body.userName;
    let userKey = req.body.key;
    let taskID = req.body.taskID;

    checkVal = checkAuth(userName, userKey);
    console.log(checkVal)
    if(checkAuth(userName, userKey) === 1)
    {
        console.log("password correct");
    }

    let returnVal = 0;
    success = true;

    let completeString = "update tasks set completed = 1 where taskID = "+taskID+";"
    console.log(completeString);

    connection.query(completeString, function(err, rows, fields) {
        if(err) res.json({success: false});

        else {
            res.json({success: true});
        }
    })

    // res.json({ taskID: taskID, completed: completed, success:success });
});

router.route('/inCompleteToDo').post(function(req, res) {
    let userName = req.body.userName;
    let userKey = req.body.key;
    let taskID = req.body.taskID;
    let success = false;

    let returnVal = 0;
    success = true;

    let completeString = "update tasks set completed = 0 where taskID = "+taskID+";"
    console.log(completeString);

    connection.query(completeString, function(err, rows, fields) {
        if(err) res.json({success: false});

        else {
            res.json({success: true});
        }
    })

});

router.route('/board').post(function(req, res) {
    let userName = req.body.userName;
    let userID = req.body.userID;
    let userKey = req.body.key;
    let boardColor = req.body.boardColor;
    let dateCreated = req.body.dateCreated;

    let boardName = req.body.boardName;

    let createBoardQuery = "insert into boards(boardName,owner,userID,boardColor,dateCreated) values (\'" + boardName + "\',\'" + userName + "\',\'" + userID + "\',"+boardColor+","+dateCreated+");"
    console.log(createBoardQuery);

    connection.query(createBoardQuery, function(err, rows, fields) {
        if (err) res.json({ success: false, boardID: 0 });
        console.log("board created");

        connection.query("SELECT LAST_INSERT_ID() as value;", function(err, rows, fields) {
            console.log(rows[0].value);
            let boardKeyReturn = rows[0].value;

            let insertDependency = "insert into memberOf(boardID,userID) values (\'" + boardKeyReturn + "\', \'"+userID+"\');"
            console.log(insertDependency);

            connection.query(insertDependency, function(err, rows, fields) {
                if (err) res.json({ success: false, boardID: 0 });

                else {
                    res.json({ success: true, boardID: boardKeyReturn });
                }
            });
        });
    });
});

router.route('/board').delete(function(req, res) { //delete board
    let userName = req.body.userName;
    let userKey = req.body.key;

    // let boardName = req.body.boardName;
    let boardID = req.body.boardID;

    let deleteString = "delete from boards where owner like \'"+userName+"\' and boardID = " + boardID + ";";
    // console.log('deleted a board with the name ' + boardName + ', with the ID '+ boardID);

    connection.query(deleteString, function(err, rows, fields) {
        if (err) res.json({success:false});

        let checkString = "select count(*) as count from boards where owner like \'"+userName+"\' and boardID = " + boardID + ";";
        
        connection.query(checkString, function(err, rows, fields) {
            if(err) res.json({success:false});

            let numRemaining = 0;

            numRemaining = rows[0].count;

            if(numRemaining === 0)
            {
                res.json({success:true});
            }
            else {
                res.json({success:false});
            }
        });
    });

});

router.route('/addUserToBoard').post(function(req, res) { //remove option to add to board twice 
    let userName = req.body.userName;
    let userKey = req.body.key;
    let userToAdd = req.body.newUserName;
    // let boardName = req.body.boardName;
    let boardID = req.body.boardID;

    // verify board exists, verify user exists
    // to add: verify userName is owner of board
    let userExistsQuery = "select count(*) as checkUser from users where userName like \'" + userToAdd + "\';";
    console.log(userExistsQuery);
    let checkUser = 0;
    connection.query(userExistsQuery, function (err, rows, fields) {
        if(err) {res.json({success: false})};

        checkUser = rows[0].checkUser;

        if(checkUser === 1)
        {
            let boardExistsQuery = "select count(*) as checkBoard from boards where boardID like \'" + boardID + "\';";
            console.log(boardExistsQuery);
            connection.query(boardExistsQuery, function (err, rows, fields) {
                if(err) {res.json({success: false})};

                checkBoard = rows[0].checkBoard;

                if(checkBoard > 0){
                    let findUserID = "select userID from users where userName like \'" + userToAdd + "\';";
                    console.log(findUserID);
                    let userID = 0;
                    connection.query(findUserID, function(err, rows, fields) {
                        if(err) {res.json({success: false})};

                        userID = rows[0].userID;

                        let addUserString = 'insert into memberOf(boardID,userID) values('+boardID+','+userID+');';
                        console.log(addUserString);
                        
                        connection.query(addUserString, function(err, rows, fields) {
                            if(err) {res.json({success: false})};

                            console.log("successfully added person to board");

                            res.json({ success: true });

                        });
                    });
                }
            });

        }
        else {
            res.json({success: false, message:"user not found"});
            console.log("Adding user to board failed, user not found")
        }
    });
});

router.route('/removeUserFromBoard').post(function(req,res) {

    let userName = req.body.userName;
    let userKey = req.body.key;
    let userToRemove = req.body.userToRemove;
    let boardID = req.body.boardID;

    //auth here

    if(userName === userToRemove)
    {
        res.json({success: false, message: "cannot remove owner from board"});
    }

    // check if user is on board

    else {
        let removeUserString = "delete from memberOf where userID in (select userID from users where userName like \'"+userToRemove+"\' and boardID like \'"+boardID+"\')";
        console.log(removeUserString);
        connection.query(removeUserString, function(err, rows, fields) {
            if (err) res.json({success: false, data:[]});

            else {
                res.json({success:true});
            }
        });
    }

});

router.route('/removeYourselfFromBoard').post(function(req,res) {
    let userName = req.body.userName;
    let userKey = req.body.key;
    let boardID = req.body.boardID;

    let findOwnerString = "select owner from boards where boardID = " + boardID + ";";
    console.log(findOwnerString);
    connection.query(findOwnerString, function(err, rows, fields) {
        if(err) res.json({success:false});

        if(userName === rows[0].owner)
        {
            res.json({success:false, message:"cannot remove owner from board"});
        }
        else {
            let removeUserString = "delete from memberOf where boardID like \'"+boardID+"\' and userID in (select userID from users where userName like \'"+userName+"\')";
            console.log(removeUserString);

            connection.query(removeUserString, function(err, rows, fields) {
                if(err) res.json({success:false});

                else {
                    res.json({success:true});
                }
            });
        }
    });
});

router.route('/getBoardData').post(function(req,res) {

    let userName = req.body.userName;
    let userKey = req.body.key;

    // auth here

    let getString = "select * from boards where boardID in (select boardID from memberOf where userID in (select userID from users where userName like \'"+userName+"\' and userKey like \'"+userKey+"\'));"; 
    console.log(getString);

    connection.query(getString, function(err, rows, fields) {
        if (err) {res.json({success:false, data: []})};

        size = rows.length;
        let returnObj = [];

        for(i = 0; i < size; i++)
        {
            returnObj.push(rows[i]);
            console.log(rows[i]);
        }
        res.json({success:true, data: returnObj});
    });
});

router.route('/toDo').post(function(req, res) {
    let userName = req.body.userName;
    let userKey = req.body.key;

    // auth here

    let boardID = req.body.boardID;
    // let creator = req.body.creator;
    let title = req.body.title;
    let description = req.body.description;
    let dueDate = req.body.dueDate;
    let reminder = req.body.reminder;
    let hasDueDate = req.body.hasDueDate;
    let hasReminder = req.body.hasReminder;
    let priority = req.body.priority;

    // console.log('create a task with the ID ' + boardID + ', created by '+ creator);

    let insertTodoString = "insert into tasks(boardID,creator,title,description,dueDate,hasDueDate,reminder,hasReminder,priority) values ("+boardID+",\'"+userName+"\',\'"+title+"\',\'"+description+"\',\'"+dueDate+"\',\'"+hasDueDate+"\',\'"+reminder+"\',\'"+hasReminder+"\',\'"+priority+"\');";
    console.log(insertTodoString)


    connection.query(insertTodoString, function(err, rows, fields) {
        if (err) res.json({success:false});

        else {
            connection.query("SELECT LAST_INSERT_ID() as value;", function(err, rows, fields) {
                if(err) res.json({success:false});

                else {
                    let taskID = rows[0].value;

                    res.json({success: true, taskID:taskID});
                }
            })
        }
    });
});

router.route('/toDo').put(function(req, res) {
    let userName = req.body.userName;
    let userKey = req.body.key;

    // auth here

    let boardID = req.body.boardID;
    let creator = req.body.creator;
    let title = req.body.title;
    let description = req.bosy.description;
    let dueDate = req.body.dueDate;
    let reminder = req.body.reminder;
    let hasDueDate = req.body.hasDueDate;
    let hasReminder = req.body.hasReminder;
    let priority = req.body.priority;
    let taskID = req.body.taskID;


    // console.log('create a task with the ID ' + boardID + ', created by '+ creator);

    let editTodoString = "update tasks set boardID = \'"+boardID+"\',creator = \'"+creator+"\',title = \'"+title+"\',description = \'"+description+"\',dueDate = \'"+dueDate+"\',hasDueDate = \'"+hasDueDate+"\',reminder = \'"+reminder+"\',hasReminder = \'"+hasReminder+"\',priority = \'"+priority+"\',completed = \'"+completed+"\' where taskID = \'"+taskID+"\'";

    connection.query(editTodoString, function(err, rows, fields) {
        if (err) res.json({success:false});

        else {
            res.json({success:true})
        }
    });
});

router.route('/getToDo').post(function(req,res) {
    let username = req.body.userName;
    let key = req.body.key;
    let boardID = req.body.boardID;
    let userID = req.body.userID;

    // let getTasksString = "select * from tasks where boardID in (select boardID from memberOf where userID like \'"+userID+"\')";
    let getTasksString = "select * from tasks where boardID = \'"+boardID+"\';";

    connection.query(getTasksString, function(err, rows, fields) {
        if (err) {res.json({success:false, data: []})};
        
        size = rows.length;
        let returnObj = [];

        for(i = 0; i < size; i++)
        {
            returnObj.push(rows[i]);
            console.log(rows[i]);
        }
        res.json({success:true, data: returnObj});
    });
});

app.listen(port);
console.log('Running on port ' + port);
