/**
 * Converts a regular image URL to a cartoon-style image URL
 * Uses cute cartoon animal images as requested by the user
 * @param {string} imageUrl - The original image URL
 * @returns {string} - The cartoon animal image URL
 */
export function getCartoonImage(imageUrl) {
  // Using Robohash.org which provides reliable cartoon animal images (set=set4)
  // Each animal is uniquely generated based on the text provided
  let animal;
  
  if (imageUrl && imageUrl.includes('brush')) {
    animal = 'fox'; // Fox for brush category
  } else if (imageUrl && imageUrl.includes('ink')) {
    animal = 'cat'; // Cat for ink category
  } else if (imageUrl && imageUrl.includes('paper')) {
    animal = 'panda'; // Panda for paper category
  } else if (imageUrl && imageUrl.includes('calligraphy')) {
    animal = 'rabbit'; // Rabbit for calligraphy works category
  } else {
    animal = 'bear'; // Bear as default
  }
  
  // Robohash.org with set=set4 provides cute cartoon animal images
  // The size parameter ensures consistent image dimensions
  return `https://robohash.org/${animal}?set=set4&size=200x200`;
}
