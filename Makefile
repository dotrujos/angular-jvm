# Detecta o JAVA_HOME automaticamente no Ubuntu
JAVA_HOME := $(shell readlink -f $$(which javac) | sed "s:/bin/javac::")

# Diretórios
JAVA_PACKAGE := src/main/java/com/gabrielaraujo/angular
JAVA_FILES := $(JAVA_PACKAGE)/Main.java
HEADER_DIR := natives/headers
SRC_DIR := natives/src
LIB_DIR := lib
TEMP_BIN := tmp

# Configurações do Compilador C++
CXX := g++
# Usando std=c++20 para suportar o std::format que você tentou usar
CXXFLAGS := -shared -fPIC -std=c++20
INCLUDES := -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(HEADER_DIR)

# Mapeamento de arquivos
CPP_FILES := $(wildcard $(SRC_DIR)/*.cpp)
OBJ_FILES := $(CPP_FILES:.cpp=.o)
TARGET := $(LIB_DIR)/libnative.so

# Regra principal
all: headers native

# 1. Geração de Headers JNI
headers: $(JAVA_FILES)
	@echo "--- Iniciando etapa Java ---"
	@mkdir -p $(HEADER_DIR) $(TEMP_BIN)
	@echo "Gerando headers em $(HEADER_DIR)..."
	javac -h $(HEADER_DIR) -d $(TEMP_BIN) $(JAVA_FILES)
	@rm -rf $(TEMP_BIN)
	@echo "Headers gerados com sucesso."

# 2. Compilação da Biblioteca Nativa (.so)
native: $(OBJ_FILES)
	@echo "--- Iniciando etapa C++ ---"
	@mkdir -p $(LIB_DIR)
	@echo "Linkando: $(TARGET)"
	$(CXX) $(CXXFLAGS) $(INCLUDES) -o $(TARGET) $(OBJ_FILES)
	@echo "Limpando arquivos objeto (.o)..."
	@rm -f $(OBJ_FILES)
	@echo "Build nativo concluído em: $(TARGET)"

# Regra genérica para transformar cada .cpp em um .o
%.o: %.cpp
	@echo "Compilando: $<"
	$(CXX) $(CXXFLAGS) $(INCLUDES) -c $< -o $@

# Limpeza total
clean:
	rm -rf $(HEADER_DIR) $(LIB_DIR) $(OBJ_FILES)

.PHONY: all headers native clean