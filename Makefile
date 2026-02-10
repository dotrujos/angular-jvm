# Ubuntu Configuration
JAVA_HOME := $(shell readlink -f $$(which javac) | sed "s:/bin/javac::")
JAVA_PACKAGE := src/main/java/com/gabrielaraujo/angular
JAVA_FILES := $(JAVA_PACKAGE)/Main.java
HEADER_DIR := natives/headers
SRC_DIR := natives/src
LIB_DIR := lib
TEMP_BIN := tmp
CXX := g++
CXXFLAGS := -shared -fPIC -std=c++20
INCLUDES := -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(HEADER_DIR)

CPP_FILES := $(wildcard $(SRC_DIR)/*.cpp)
OBJ_FILES := $(CPP_FILES:.cpp=.o)
TARGET := $(LIB_DIR)/libnative.so

all: headers native

headers: $(JAVA_FILES)
	@echo "--- Iniciando etapa Java ---"
	@mkdir -p $(HEADER_DIR) $(TEMP_BIN)
	@echo "Gerando headers em $(HEADER_DIR)..."
	javac -h $(HEADER_DIR) -d $(TEMP_BIN) $(JAVA_FILES)
	@rm -rf $(TEMP_BIN)
	@echo "Headers gerados com sucesso."

native: $(OBJ_FILES)
	@echo "--- Iniciando etapa C++ ---"
	@mkdir -p $(LIB_DIR)
	@echo "Linkando: $(TARGET)"
	$(CXX) $(CXXFLAGS) $(INCLUDES) -o $(TARGET) $(OBJ_FILES)
	@echo "Limpando arquivos objeto (.o)..."
	@rm -f $(OBJ_FILES)
	@echo "Build nativo concluído em: $(TARGET)"

%.o: %.cpp
	@echo "Compilando: $<"
	$(CXX) $(CXXFLAGS) $(INCLUDES) -c $< -o $@

clean:
	rm -rf $(HEADER_DIR) $(LIB_DIR) $(OBJ_FILES)

.PHONY: all headers native clean