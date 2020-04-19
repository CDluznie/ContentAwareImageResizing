# Content Aware image resizing

implementation with seam carving

![tower](./doc/results/tower.png)

![resize-tower](./doc/results/tower_resize.png)

![result-tower](./doc/results/tower_result.png)

The tool can only reduce width of images but with an implementation of another graph generation based on vertical gradient the reduction of the height of the image is possible

## Usage

- compile :
	* mkdir bin/
	* javac -d bin/ @files

- generate documentation :
	* javadoc -d docs/api @files


- run :
	* java -cp bin/ cair.main.Main

