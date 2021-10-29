const router = require('express').Router()
require('dotenv').config();
const nodemailer = require('nodemailer');
const {v4:uuidv4} = require("uuid");

//Post request that sends the email
/*
    expects
    { 
        userEmail: email
    }
*/
router.post("/", (req, res) => {
    //make transporter ( sender)
    const uniqueCode = uuidv4();
    const transporter = nodemailer.createTransport({
        service:"Gmail",
        auth: {
            user: process.env.VERIFICATION_EMAIL,
            pass: process.env.VERIFICATION_PASSWORD
        }
    })
    if(!transporter){
        res.json({message:"internal server error"});
    }

    const emailBody =`Hi! \n Please verify your account by clicking the link below: \n http://localhost:3000/api/verification/${uniqueCode}`;
    //options for who we are sending it to
    // console.log(process.env.VERIFICATION_EMAIL,process.env.VERIFICATION_PASSWORD);
    const options = {
        from: process.env.VERIFICATION_EMAIL,
        to: req.body.userEmail,
        subject: "Account Verification",
        text:`${emailBody}`
    }
    transporter.sendMail(options, (err, res) => {
        if(err){
            console.log(err);
            res.status(500).json(err);
            return;
        }
        console.log("Sent:" + res.response);
        res.status(200).json({"data": res.response})

    })
    res.status(200).json({"message":`Verification email sent to ${req.body.userEmail}`});
})

//this endpoint is used for the verification link
router.get("/:code", (req, res) => {
    console.log(req.params.code);
    //here we need to check if the db contains that code
    //and activate the account
    //for that we need to det up the account
    res.status(200).send("Your account has been activated!");
})
module.exports = router 