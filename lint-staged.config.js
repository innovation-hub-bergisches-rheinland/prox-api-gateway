const path = require("path");
module.exports = {
  "*.java": [
    absolutePaths =>
      `mvn spotless:apply -DspotlessFiles=${absolutePaths.join(",")}`,
    "git add"
  ],
  "*.{js,ts,css,scss,json,md,html,yml,yaml}": ["prettier --write", "git add"]
};
