JAVA_HOME := $(shell readlink -f $$(which javac) | sed "s:/bin/javac::")

HEADER_DIR := natives/headers
SRC_DIR := natives/src
LIB_DIR := lib
TARGET := $(LIB_DIR)/libnative.so 

CXX := g++
CXXFLAGS := -shared -fPIC -std=c++20
INCLUDES := -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(HEADER_DIR)

CPP_FILES := $(wildcard $(SRC_DIR)/*.cpp)
OBJ_FILES := $(CPP_FILES:.cpp=.o)

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
	rm -rf $(LIB_DIR) $(OBJ_FILES)
	# Opcional: limpar headers gerados pelo Maven
	# rm -rf $(HEADER_DIR) 

.PHONY: native clean