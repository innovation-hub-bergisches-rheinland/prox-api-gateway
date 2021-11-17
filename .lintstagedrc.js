const path = require("path");

module.exports = {
  // Delegate the whole formatting to spotless
  "*": [
    (absolutePaths) => {
      let resolvedPaths = absolutePaths;

      if (process.platform === "win32") {
        console.log("ABCDEFG");
        resolvedPaths = absolutePaths
          .map((file) => path.resolve(file))
          .map((file) => file.split("\\").join("\\\\"));
        console.log(resolvedPaths);
        return `./mvnw.cmd spotless:apply -X -DspotlessFiles=${resolvedPaths.join(
          ","
        )}`;
      }

      return `./mvnw spotless:apply -X -DspotlessFiles=${resolvedPaths.join(
        ","
      )}`;
    },
  ],
};
