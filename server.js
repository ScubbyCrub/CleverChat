const express = require('express');
const theRoutes = require('./backend/index.js')
const app = express();
const PORT = 3000; // this is to be changed to dynamically work with the heroko port 

app.use(express.json());
app.use(theRoutes);
app.use("/", (req, res) => {
    res.send("Hello");
})

app.listen(PORT, () => {
    console.log(`Server started, listeing on port ${PORT}`);
});