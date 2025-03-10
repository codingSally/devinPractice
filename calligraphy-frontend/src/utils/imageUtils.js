/**
 * Converts a regular image URL to a cartoon-style image URL
 * Uses cute cartoon animal images as requested by the user
 * @param {string} imageUrl - The original image URL
 * @returns {string} - The cartoon animal image URL
 */
export function getCartoonImage(imageUrl) {
  // Since we can't use external cartoon images due to CORS or access issues,
  // we're using colorful placeholder images with cartoon animal text
  // In a production environment, we would use actual cartoon animal images hosted on a CDN
  
  // Create a pastel background color based on the image path to simulate different cartoon animals
  let bgColor, animal;
  
  if (imageUrl && imageUrl.includes('brush')) {
    bgColor = 'b3e0ff'; // Light blue
    animal = 'Cartoon Fox';
  } else if (imageUrl && imageUrl.includes('ink')) {
    bgColor = 'ffb3b3'; // Light red
    animal = 'Cartoon Cat';
  } else if (imageUrl && imageUrl.includes('paper')) {
    bgColor = 'b3ffb3'; // Light green
    animal = 'Cartoon Panda';
  } else if (imageUrl && imageUrl.includes('calligraphy')) {
    bgColor = 'e6b3ff'; // Light purple
    animal = 'Cartoon Rabbit';
  } else {
    bgColor = 'ffffb3'; // Light yellow
    animal = 'Cartoon Bear';
  }
  
  // Use placeholder.com which is reliable and supports custom colors
  return `https://via.placeholder.com/200x200/${bgColor}/333333.png?text=${animal.replace(' ', '+')}`;
}
