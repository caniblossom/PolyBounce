#version 150                                                
                                                            
uniform mat4 projection;                                    
uniform mat4 view;                                          
                                                            
in vec3 positionIn;                                         
in vec3 colorIn;                                            
in vec3 normalIn;                                           
                                                            
out vec3 color;                                             
out vec3 normal;                                            
out vec3 position;                                          
                                                            
void main() {                                                                                                      
    color = colorIn;                                        
    normal = normalIn;                                      
                                                            
    position = positionIn;                                  
    gl_Position = projection * view * vec4(position, 1.0f); 
}
     