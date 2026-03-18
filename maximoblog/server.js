const express = require('express');
const path = require('path');
const app = express();

const PORT = process.env.PORT || 8080;

// Path to Angular build (after running `ng build --prod`)
const DIST_FOLDER = path.join(__dirname, '../frontend/dist/MaximoBlogFront');
app.use(express.static(DIST_FOLDER));

app.get('*', (req, res) => {
    res.sendFile(path.join(DIST_FOLDER, 'index.html'));
});

app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});