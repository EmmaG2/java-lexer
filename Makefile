SRC_DIR = src/main/java
OUT_DIR = dist

run: compile
	java -cp $(OUT_DIR) lexer.Main

compile:
	@mkdir -p $(OUT_DIR)
	javac -d $(OUT_DIR) $(SRC_DIR)/lexer/*.java

clean:
	rm -rf $(OUT_DIR)

.PHONY: run compile clean
