const path = require("path");
module.exports = {
  "*.java": [
    absolutePaths => {
      const resolvedPaths = absolutePaths.map(file => path.resolve(file));
      return `mvn spotless:apply -DspotlessFiles=${resolvedPaths.join(",")}`;
    },
    "git add"
  ],
  "*.{js,ts,css,scss,json,md,html,yml,yaml}": ["prettier --write", "git add"]
};
