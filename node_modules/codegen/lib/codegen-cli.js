#!/usr/bin/env node

var codegen = require("./codegen");
var fs = require("fs");
var path = require("path");
var yaml = require("js-yaml");

if (process.argv.length != 5) {
	console.log("usage: codegen data_file template_dir output_dir");
	process.exit(1);
	return;
}

var dataPath = process.argv[2];
var templatePath = process.argv[3];
var outputPath = process.argv[4];

//var templatesPath = "templates/angular.tmpl";
//var dataPath = "data/project.yaml";
//

var data = yaml.safeLoad(fs.readFileSync(dataPath));

// Configures the code generator
codegen.configure({
	templateBase: templatePath,
	outputBase: outputPath
});

var templateFile = path.resolve(".", path.join(templatePath, "codegen.template.js"));
var template = require(templateFile);

template(codegen, data);

