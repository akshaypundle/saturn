module.exports = function(grunt) {
  "use strict";

  grunt.initConfig({
    srcRoot: ".",
    libraryRoot: "<%= srcRoot %>/libraries",
    moduleRoot: "<%= srcRoot %>/modules",
    clean: {
      build: ["<%= moduleRoot %>/**/*.js", "<%= moduleRoot %>/**/*.js.map", "<%= srcRoot %>/styles/app.css"]
    },

    csslint: {
      lint: {
        options: {
          "adjoining-classes": false, 
          "box-model": false,
          "box-sizing": false, 
          "bulletproof-font-face": false, 
          "display-property-grouping": false, 
          "duplicate-background-images": false,
          "fallback-colors": false, 
          "floats": false, 
          "font-faces": false, 
          "font-sizes": false, 
          "gradients": false, 
          "ids": false,
          "import": false, 
          "important": false, 
          "known-properties": false, 
          "outline-none": false,
          "overqualified-elements": false, 
          "qualified-headings": false,
          "unique-headings": false,
          "vendor-prefix": false, 
          "zero-units": false 
        },
        src: [
          "<%= srcRoot %>/styles/app.css"
        ]
      }
    },

    less: {
      compile: {
        files: {
          "<%= srcRoot %>/styles/app.css": "<%= srcRoot %>/styles/app.less"
        }
      }
    },

    ngtemplates: {
      "datatable": {
        cwd: "<%= srcRoot %>",
        dest: "<%= moduleRoot %>/templates.js",
        src: "modules/**/*.html",
      }
    },

    ts: {
      compile: {
        options: {
          comments: true,
          module: "amd",
          noImplicitAny: true,
          sourceMap: true,
          target: "es5"
        },
        src: ["<%= moduleRoot %>/**/*.ts", "<%= srcRoot %>/typings/**/*.d.ts", "<%= libraryRoot %>/**/*.d.ts"]
      }
    },

    tslint: {
      options: {
        configuration: grunt.file.readJSON("tslint.json")
      },
      files: ["<%= moduleRoot %>/**/*.ts"]
    },

    watch: {
      options: {
        force: true
      },
      gruntfile: {
        files: "Gruntfile.js",
        tasks: ["build"]
      },
      ts: {
        files: ["<%= moduleRoot %>/**/*.ts", "<%= srcRoot %>/typings/**/*.d.ts", "<%= libraryRoot %>/**/*.d.ts"],
        tasks: ["tslint", "ts"]
      },
      less: {
        files: ["<%= moduleRoot %>/**/*.less"],
        tasks: ["less", "csslint"]
      },
      templates: {
        files: ["<%= moduleRoot %>/**/*.html"],
        tasks: ["ngtemplates"]
      }
    }
  });
  
  grunt.loadNpmTasks("grunt-angular-templates");
  grunt.loadNpmTasks("grunt-blanket-mocha");
  grunt.loadNpmTasks("grunt-contrib-clean");
  grunt.loadNpmTasks("grunt-contrib-csslint");
  grunt.loadNpmTasks("grunt-contrib-less");
  grunt.loadNpmTasks("grunt-contrib-watch");
  grunt.loadNpmTasks("grunt-ts");
  grunt.loadNpmTasks("grunt-tslint");

  
  grunt.registerTask("default", "build");

  grunt.registerTask("build",
    [
      "clean",
      "ngtemplates",
      "less",
      "csslint",
      "allFilesCovered",
      "tslint",
      "ts"
    ]
  );
  grunt.registerTask("devWatch",
    [
      "build",
      "watch"
    ]
  );
};

