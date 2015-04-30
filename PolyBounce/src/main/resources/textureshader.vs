#version 150                                                
                                                            
uniform mat4 view;                                          
                                                            
in vec2 positionIn;                                         
in vec2 texCoordIn;                                            
                                                            
out vec2 texCoord;
                                                            
void main() {                                                                                                      
    texCoord = texCoordIn;
    gl_Position = view * vec4(positionIn, -1.0f, 1.0f); 
}
     