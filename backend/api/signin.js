const router = require("express").Router();

/**
 * this will handle the signin routes
 */

router.get("/", (req, res) => {
    res.status(200).send("Get request sent to sign in  endpoint");
})

router.post("/", (req, res) => {
    res.status(200).send("Post request sent to sign in  endpoint");
})

router.put("/:id", (req, res) => {
    res.status(200).send("Put request sent to sign inendpoint");
})

router.delete("/:id", (req, res) => {
    res.status(200).send("Delete request sent to sign in endpoint");
})


module.exports = router;


